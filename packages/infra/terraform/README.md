# WD&S Infa

1. Create `.env` file with data:
```
# AWS
export AWS_ACCESS_KEY_ID="<AWS_ACCESS>"
export AWS_SECRET_ACCESS_KEY="<AWS_SECRET>"
export AWS_REGION="<AWS_REGION>"

# FLY.IO
export FLY_API_TOKEN="<FLY_TOKEN>"

```

2. Source the requested environment `source .env`
3. For staging infra do:`terraform workspace select staging`. For prod use `terraform workspace select prod`

4. Run fly.io proxy: `flyctl machines api-proxy`

5. Run `terraform plan`

# Connecting to remote db on fly.io

1. Create the tunnel by running `flyctl proxy <YOUR_LOCAL_PORT>:5432 --app wds-db-{env}`

# Connecting to remote machine via ssh

1. Issue a new ssh tokens for you by running: `flyctl ssh issue --agent`
2. Run `flyctl ssh console wds-db-{env}.internal --app wds-db-{env}`
