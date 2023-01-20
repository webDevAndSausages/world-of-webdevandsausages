resource "fly_app" "frontend" {
  name       = "wds-frontend-${terraform.workspace}"
  org        = "wds"
}

resource "fly_machine" "frontend_machine" {
  app    = fly_app.frontend.id
  image  = "nginx" # initial image for testing purposes
  name   = "wds-frontend-${terraform.workspace}"
  region = "fra"

  services = [
    {
      protocol      = "tcp"
      internal_port = 443
      ports         = [
        {
          port     = 443
          handlers = ["tls", "http"]
        }
      ]
    },
    {
      protocol      = "tcp"
      internal_port = 80
      ports         = [
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

resource "fly_ip" "frontend_ip" {
  app  = fly_app.frontend.id
  type = "v4"
}

resource "fly_cert" "frontend_cert" {
  app      = fly_app.frontend.id
  hostname = "${terraform.workspace == "prod" ? "*" : terraform.workspace}.webdevandsausages.org"
}

resource "fly_cert" "frontend_cert_2" {
  count = terraform.workspace == "prod" ? 1 : 0
  app      = fly_app.frontend.id
  hostname = "webdevandsausages.org"
}
