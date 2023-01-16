resource "fly_app" "db" {
  name = "wds-db-${terraform.workspace}"
  org = "wds"
}

resource "fly_machine" "db_machine" {
  app    = fly_app.db.id
  image  = "postgres:10.6"
  name   = "wds-db-${terraform.workspace}"
  region = "fra"
  env    = {
    POSTGRES_PASSWORD = "password"
    POSTGRES_USER     = "wds"
    POSTGRES_DB       = "wds_db"
  }
  services = []

  mounts = [
    {
      volume = fly_volume.db_storage.id
      path = "/var/lib/postgresql"
    }
  ]

  lifecycle {
    ignore_changes = [
      image
    ]
  }
}

resource "fly_volume" "db_storage" {
  name   = "wds_db_volume_${terraform.workspace}"
  app    = fly_app.db.id
  size   = 2
  region = "fra"
}

resource "fly_app" "backend" {
  name       = "wds-backend-${terraform.workspace}"
  depends_on = [fly_app.db]
  org = "wds"
}

resource "fly_ip" "backend_ip" {
  app  = fly_app.backend.id
  type = "v4"
}

resource "fly_machine" "backend_machine" {
  app    = fly_app.backend.id
  image  = "nginx" # initial image for testing purposes
  name   = "wds-backend-${terraform.workspace}"
  region = "fra"

  env = {
    DB_URL = "jdbc:postgresql://wds-db-${terraform.workspace}.internal:5432/wds_db"
  }

  services = [
    {
      protocol      = "tcp"
      internal_port = 5000
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