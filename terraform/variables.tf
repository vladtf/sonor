variable "image_tag" {
  description = "Tag to use for the Docker images"
  type        = string
  default     = "latest"
}

variable "db_user" {
  description = "Database user"
  type        = string
  default     = "mobylab-app"
}

variable "db_password" {
  description = "Database password"
  type        = string
  default     = "mobylab-app"
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "mobylab-app"
}

variable "authentication_secret" {
  description = "Secret key for authentication"
  type        = string
  default     = "your_secret_key"
}