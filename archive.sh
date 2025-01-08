#!/bin/bash

# Define the root directory of the repository
REPO_PATH="./"

# Define the output archive file name
OUTPUT_ARCHIVE="./docker_tf_files.zip"

# Define the path to README file
README_FILE="./README.md"

# Ensure the output directory exists
OUTPUT_DIR=$(dirname "$OUTPUT_ARCHIVE")
mkdir -p "$OUTPUT_DIR"

# Create the archive by directly finding the files and adding them
echo "Creating archive at: $OUTPUT_ARCHIVE"
(
    cd "$REPO_PATH" || exit
    zip -r "$OUTPUT_ARCHIVE" README.md $(find . -type f \( -name "Dockerfile" -o -name "*.tf" \)) > /dev/null 2>&1
)

# Verify if the archive was created
if [ -f "$OUTPUT_ARCHIVE" ]; then
    echo "Archive successfully created."
else
    echo "Error: Archive was not created."
    exit 1
fi

# Display the contents of the archive
echo "Contents of the archive:"
unzip -l "$OUTPUT_ARCHIVE"

echo "Archive created successfully: $OUTPUT_ARCHIVE"
