resource "fly_app" "db" {
  name = "wds-db-${terraform.workspace}"
  org  = "wds"
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
      path   = "/var/lib/postgresql"
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


resource "fly_machine" "db_backup_scheduler" {
  app    = fly_app.db.id
  image  = "nginx" # initial image for testing purposes
  name   = "wds-db-backup-${terraform.workspace}"
  region = "fra"

  services = []

  lifecycle {
    ignore_changes = [
      image
    ]
  }
}
