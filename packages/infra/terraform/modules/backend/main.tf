resource "fly_app" "backend" {
  name       = "wds-backend-${terraform.workspace}"
  depends_on = [fly_app.db]
  org        = "wds"
}

resource "fly_machine" "backend_machine" {
  app    = fly_app.backend.id
  image  = "nginx" # initial image for testing purposes
  name   = "wds-backend-${terraform.workspace}"
  region = "fra"

  env = {
    DB_URL = "jdbc:postgresql://wds-db-${terraform.workspace}.internal:5432/wds_db"
  }

  services = []

  lifecycle {
    ignore_changes = [
      image
    ]
  }
}
