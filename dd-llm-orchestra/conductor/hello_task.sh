curl -sS -X POST "http://localhost:8080/api/metadata/taskdefs" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "name": "HELLO_TASK",
      "description": "Hello World task",
      "retryCount": 0,
      "timeoutSeconds": 60,
      "timeoutPolicy": "TIME_OUT_WF",
      "ownerEmail": "you@example.com"
    }
  ]'
