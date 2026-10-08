"""Vérification HTTP locale de la démo ShopWise (Python 3, bibliothèque standard).

Crée puis supprime ses propres données. Ne jamais lancer contre une production.
Usage : python3 scripts/verify_api.py http://127.0.0.1:18088
"""
import json
import sys
import uuid
import base64
from decimal import Decimal
from urllib.request import Request, urlopen
from urllib.error import HTTPError
from urllib.parse import urlparse

base = sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:18088"
if urlparse(base).hostname not in {"localhost", "127.0.0.1", "::1"}:
    raise SystemExit("Ce script est réservé à une instance locale de démonstration.")
checks = 0

def call(method, path, expected, token=None, data=None, raw=None):
    global checks
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    payload = raw.encode() if raw is not None else (json.dumps(data).encode() if data is not None else None)
    try:
        response = urlopen(Request(base + path, data=payload, headers=headers, method=method), timeout=30)
    except HTTPError as error:
        response = error
    body = response.read().decode()
    assert response.status == expected, (method, path, expected, response.status, body)
    parsed = json.loads(body, parse_float=Decimal) if body else None
    if expected >= 400:
        assert isinstance(parsed, dict) and parsed["status"] == expected and parsed["message"], (path, body)
    checks += 1
    print(f"OK {method} {path} : {expected}")
    return parsed

def login(email, password="password", expected=200):
    return call("POST", "/api/auth/login", expected, data={"email": email, "password": password})

admin = login("marie.dupont@shopwise.test")["token"]
user = login("lucas.martin@shopwise.test")["token"]
header, payload, signature = user.split('.')
claims = json.loads(base64.urlsafe_b64decode(payload + '=' * (-len(payload) % 4)))
claims['role'] = 'ROLE_ADMIN'
forged_payload = base64.urlsafe_b64encode(json.dumps(claims).encode()).rstrip(b'=').decode()
call("GET", "/api/users", 401, header + '.' + forged_payload + '.' + signature)
none_header = base64.urlsafe_b64encode(b'{"alg":"none","typ":"JWT"}').rstrip(b'=').decode()
call("GET", "/api/users", 401, none_header + '.' + forged_payload + '.')
login("marie.dupont@shopwise.test", "wrong", 401)
login("nobody@example.test", "password", 401)
call("POST", "/api/auth/login", 400, data={})
for route in ("products", "categories", "sales", "users", "recommendations"):
    call("GET", "/api/" + route, 401)
    call("GET", "/api/" + route, 401, "invalid")
    call("GET", "/api/" + route, 200, admin)
    call("GET", "/api/" + route, 403 if route == "users" else 200, user)
for route, minimum in (("products", 4), ("categories", 3), ("users", 2), ("sales", 2)):
    assert len(call("GET", "/api/" + route, 200, admin)) >= minimum, "Données de démonstration absentes"
    for method, suffix in (("POST", ""), ("PUT", "/1"), ("DELETE", "/1")):
        call(method, "/api/" + route + suffix, 401, data={} if method != "DELETE" else None)
        call(method, "/api/" + route + suffix, 403, user, {} if method != "DELETE" else None)
    call("GET", "/api/" + route + "/1", 401)
    call("GET", "/api/" + route + "/1", 403 if route == "users" else 200, user)
    call("GET", "/api/" + route + "/999999999", 404, admin)
    call("GET", "/api/" + route + "/invalid", 400, admin)
    call("DELETE", "/api/" + route + "/999999999", 404, admin)
    call("POST", "/api/" + route, 400, admin, {})
    call("POST", "/api/" + route, 400, admin, raw="{")

tag = "QA-" + uuid.uuid4().hex[:10]
created = []
try:
    records = {
        "categories": {"name": tag},
        "users": {"firstName": "Évaluation", "lastName": "Test", "email": tag + "@example.test", "password": "password", "role": "USER"},
        "products": {"name": tag, "sku": tag, "description": "Produit de vérification", "price": 3.25},
    }
    ids = {}
    for route, payload in records.items():
        result = call("POST", "/api/" + route, 201, admin, payload)
        ids[route] = result["id"]
        created.append((route, result["id"]))
        assert "password" not in result and "passwordHash" not in result
        call("GET", f"/api/{route}/{result['id']}", 200, admin)
        call("POST", "/api/" + route, 409, admin, payload)
        revised = dict(payload)
        revised["firstName" if route == "users" else "name"] = tag + " modifié"
        updated = call("PUT", f"/api/{route}/{result['id']}", 200, admin, revised)
        assert updated["firstName" if route == "users" else "name"] == tag + " modifié"
        call("PUT", f"/api/{route}/999999999", 404, admin, revised)
    previous_token = login(records["users"]["email"])["token"]
    call("PUT", f"/api/users/{ids['users']}", 200, admin, dict(records["users"], role="ADMIN"))
    call("GET", "/api/products", 401, previous_token)
    elevated_token = login(records["users"]["email"])["token"]
    call("GET", "/api/users", 200, elevated_token)
    call("PUT", f"/api/users/{ids['users']}", 200, admin, records["users"])
    call("GET", "/api/users", 401, elevated_token)
    previous_token = login(records["users"]["email"])["token"]
    call("PUT", f"/api/users/{ids['users']}", 200, admin, dict(records["users"], password="new-password"))
    call("GET", "/api/products", 401, previous_token)
    login(records["users"]["email"], "password", 401)
    deleted_token = login(records["users"]["email"], "new-password")["token"]
    call("POST", "/api/users", 400, admin, dict(records["users"], email="invalid", role="ROOT"))
    call("POST", "/api/products", 400, admin, dict(records["products"], price=-1))
    sale = {"userId": ids["users"], "items": [{"productId": ids["products"], "quantity": 2}, {"productId": 2, "quantity": 1}]}
    result = call("POST", "/api/sales", 201, admin, sale)
    sale_id = result["id"]
    created.append(("sales", sale_id))
    assert result["totalPrice"] == Decimal("11.00")
    assert sum(x["lineTotal"] for x in result["items"]) == result["totalPrice"]
    call("GET", f"/api/sales/{sale_id}", 200, user)
    assert call("GET", "/api/sales", 200, admin)[0]["id"] == sale_id
    call("PUT", f"/api/products/{ids['products']}", 200, admin, dict(records["products"], price=4.25))
    assert call("GET", f"/api/sales/{sale_id}", 200, admin)["totalPrice"] == Decimal("11.00"), "Prix historique modifié"
    assert call("PUT", f"/api/sales/{sale_id}", 200, admin, sale)["totalPrice"] == Decimal("13.00")
    call("PUT", "/api/sales/999999999", 404, admin, sale)
    for route, identifier in (("products", ids["products"]), ("users", ids["users"])):
        call("DELETE", f"/api/{route}/{identifier}", 409, admin)
    call("POST", "/api/sales", 400, admin, dict(sale, items=[]))
    call("POST", "/api/sales", 400, admin, dict(sale, items=[{"productId": 1, "quantity": 0}]))
    call("POST", "/api/sales", 404, admin, dict(sale, userId=999999999))
    call("POST", "/api/sales", 404, admin, dict(sale, items=[{"productId": 999999999, "quantity": 1}]))
    for query in ("", "?productId=1", "?productId=1&limit=1", "?limit=20", f"?productId={ids['products']}"):
        recommendations = call("GET", "/api/recommendations" + query, 200, user)
        product_ids = [x["productId"] for x in recommendations]
        assert len(product_ids) == len(set(product_ids))
        if query.startswith("?productId=1"):
            assert 1 not in product_ids
        if "limit=1" in query:
            assert len(product_ids) <= 1
    for query in ("?limit=0", "?limit=21", "?limit=abc", "?productId=abc", "?productId=-1"):
        call("GET", "/api/recommendations" + query, 400, admin)
    call("GET", "/api/recommendations?productId=999999999", 404, admin)
    call("GET", "/api/unknown", 404, admin)
    call("PATCH", "/api/products/1", 405, admin)
finally:
    for route, identifier in reversed(created):
        call("DELETE", f"/api/{route}/{identifier}", 204, admin)
        call("GET", f"/api/{route}/{identifier}", 404, admin)
        if route == "users" and 'deleted_token' in globals():
            call("GET", "/api/products", 401, deleted_token)

print(f"BILAN : {checks} requêtes HTTP vérifiées ; données temporaires supprimées.")
