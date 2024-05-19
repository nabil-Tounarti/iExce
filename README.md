# Solution pour les Services Liés à IPFS
## Introduction

### Ce projet propose une solution pour gérer des fichiers en utilisant IPFS et MongoDB via deux services distincts :

    Service 1 (Java) : Charge des documents sur IPFS, notifie le Service 2 et permet de récupérer un fichier à partir de son CID.
    Service 2 (Go) : Reçoit les notifications du Service 1, stocke les identifiants des nouveaux fichiers dans MongoDB et offre une route pour lister les identifiants connus.

### Schéma de la Solution

#### Service 1 (Java)

    Fonctionnalités :
        Mettre en ligne des documents sur IPFS.
        Notifier le Service 2 de cette mise en ligne via Kafka.
        Permettre de récupérer un fichier à partir de son CID.

#### Service 2 (Go)

    Fonctionnalités :
        Recevoir une notification du Service 1 via Kafka.
        Stocker l'identifiant du nouveau fichier dans MongoDB.
        Offrir une route pour lister les identifiants connus.
## Architecture
![Untitled(13)](https://github.com/nabil-Tounarti/iExce/assets/117689544/a5653d94-0c53-4f3a-a6e8-42cedc7ca092)



    Client -> Service 1: POST /upload
    Service 1 -> IPFS: Upload Document
    IPFS -> Service 1: Return CID
    Service 1 -> Kafka: Notify with CID

    Client -> Service 1: GET /get-file/{cid}
    Service 1 -> IPFS: Retrieve Document
    IPFS -> Service 1: Return Document
    Service 1 -> Client: Return Document

![Untitled(15)](https://github.com/nabil-Tounarti/iExce/assets/117689544/0d90da56-e637-4ac2-a482-45e6cd8454ad)



    Client -> Service 2: GET /list-cids
    Service 2 -> MongoDB: Retrieve CIDs
    MongoDB -> Service 2: Return CIDs
    Service 2 -> Client: Return CIDs

    Kafka -> Service 2: CID Message
    Service 2 -> MongoDB: Store CID
    
![Untitled(14)](https://github.com/nabil-Tounarti/iExce/assets/117689544/5b6c12fb-f94c-4452-9de7-76c9bf6bcae7)





