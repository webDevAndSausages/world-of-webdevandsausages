terraform {
  required_providers {
    fly = {
      source = "fly-apps/fly"
      version = "0.0.20"
    }
    aws = {
      source = "hashicorp/aws"
      version = "4.49.0"
    }
    cockroach = {
      source = "cockroachdb/cockroach"
      version = "0.1.0"
    }
  }
}

