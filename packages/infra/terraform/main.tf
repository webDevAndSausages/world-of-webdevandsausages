resource "cockroach_cluster" "database" {
  cloud_provider = "GCP"
  name           = "wds-db-${terraform.workspace}"
  create_spec    = {
    serverless = {
      regions     = ["europe-west1"]
      spend_limit = 100000 #Amount in cents 100,000 = 100 USD
    }
  }
}

resource "aws_s3_bucket" "frontend" {
  bucket = "wds-frontend-${terraform.workspace}"
}

resource "aws_s3_bucket_acl" "frontent_acl" {
  bucket = aws_s3_bucket.frontend.id
  acl    = "public-read"
}

resource "aws_s3_bucket_website_configuration" "frontend_website_config" {
  bucket = aws_s3_bucket.frontend.id

  index_document {
    suffix = "index.html"
  }
}


resource "fly_app" "backend" {
  name = "wds-backend-${terraform.workspace}"
}

resource "fly_ip" "backend_ip" {
  app  = fly_app.backend.id
  type = "v4"
}

resource "fly_machine" "backend_machine" {
  app      = fly_app.backend.id
  image    = "nginx" # initial image for testing purposes
  name     = "wds-backend-${terraform.workspace}"
  region   = "ord"
  services = [
    {
      protocol      = "tcp"
      internal_port = 80
      ports         = [
        {
          port     = 443
          handlers = ["tls", "http"]
        },
        {
          port     = 80
          handlers = ["http"]
        }
      ]
    }
  ]

  lifecycle {
    ignore_changes = [
      image
    ]
  }
}
