# WD&S Infa

1. Create `.env` file with data:
```

# FLY.IO
export FLY_API_TOKEN="<FLY_TOKEN>"

```

2. Source the requested environment `source .env`
3. For staging infra do:`terraform workspace select staging`. For prod use `terraform workspace select prod`

4. Run fly.io proxy: `flyctl machines api-proxy`

5. Run `terraform plan`
