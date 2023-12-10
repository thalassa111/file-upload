## Assignment file-upload
This project is a part of course Java Spring boot and the assignment is to create a file uploading API similar to google drive/dropbox. I saved my database locally with postgresql.

Grade G
Has to be able to:

1. Register a user
2. Log in with a user
3. Create a new folder(as a logged in user)
4. Upload a file to a specific folder(as a logged in user)
5. Delete file(as a logged in user)
6. Download file(as a logged in user)

Grade VG

1. Everything for grade G
2. Comment all code with Java standard
3. Write at least three tests:
    - Test user registration
    - Test file upload
    - Test file deletion

## Endpoints

### user/register [POST]

Register a new user

Example body JSON:
```
{
   "name": "Manolo",
   "email": "manolo@latino.com",
   "address": "estrella road 44",
   "password": "hello"
}
```
---
### user/register [GET]
Login/Generate token

Example body JSON:
```
{
    "email": "manolo@latino.com",
    "password": "hello"
}
```
---
### folder/create-folder [POST]
Create a folder, requires "Authorization" token

Example body JSON:
```
{
    "folderName": "testfolder"
}
```
---
### file/upload-file/{folderId} [POST]
Upload file, requires "Authorization" token

Example body:
```
Body->form-data
Key   Value
file  SelectFile
```
---
### file/delete-file/{folderId}/{fileName} [DELETE]
Delete file, requires "Authorization" token, takes the folderId and name of file, user will be extracted from token.

---
### file/download-file/{folderId}/{fileName} [DELETE]
Download file, requires "Authorization" token, takes the folderId and name of file, user will be extracted from token.
