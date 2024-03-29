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
    github = {
      source = "integrations/github"
      version = "5.16.0"
    }
  }
}

