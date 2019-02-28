#!/bin/bash
curl -X POST -H "Content-Type: application/json" \
    -d '{"username": "dennis@bipedalprogrammer.com", "password": "secret"}' \
    http://localhost:8080/api/registration