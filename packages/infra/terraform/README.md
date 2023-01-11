# WD&S Infa

1. Create `.env.staging` and `.env.prod` files with data:
```
export AWS_ACCESS_KEY_ID="<AWS_ACCESS>"
   export AWS_SECRET_ACCESS_KEY="<AWS_SECRET>"
   export AWS_REGION="<AWS_REGION>"

# FLY.IO
export FLY_API_TOKEN="<FLY_TOKEN>"

# COCKROACH DB
export COCKROACH_API_KEY="<API_KEY>"
```

2. Source the requested environment `source .env.staging`
3. For staging infra do:`terraform workspace select staging`

4. Run fly.io proxy: `flyctl machines api-proxy`

5. Run `terraform plan`
