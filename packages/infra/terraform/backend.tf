terraform {
  backend "s3" {
    bucket = "wds-tf-state"
    key    = "terraform"
    region = "eu-west-1"
  }
}
