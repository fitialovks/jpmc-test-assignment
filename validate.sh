#!/usr/bin/env bash

curl -s \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  -X POST \
  -d '{"accountNumber": "acc", "sources": ["sample"]}' \
  'http://localhost:8080/v1/api/account/validate' | jq .
