# Solution pour les Services Liés à IPFS
## Comment utiliser cette solution
1. Localisez-vous dans le répertoire racine du projet où se trouve le fichier Docker Compose.
2. Tapez la commande "docker-compose up" pour démarrer les six conteneurs.
3. Vérifiez que tous les six conteneurs démarrent correctement.
4. Si le conteneur "go-service" ne démarre pas, exécutez la commande "docker-compose up go-service" spécifiquement pour ce conteneur.
5. Une fois tous les conteneurs démarrés, lancez l'interface frontend.

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


### Service 1 (Java)

    Client -> Service 1: POST /upload
    Service 1 -> IPFS: Upload Document
    IPFS -> Service 1: Return CID
    Service 1 -> Kafka: Notify with CID

    Client -> Service 1: GET /get-file/{cid}
    Service 1 -> IPFS: Retrieve Document
    IPFS -> Service 1: Return Document
    Service 1 -> Client: Return Document

![Untitled(15)](https://github.com/nabil-Tounarti/iExce/assets/117689544/0d90da56-e637-4ac2-a482-45e6cd8454ad)


### Service 2 (Go)

    Client -> Service 2: GET /list-cids
    Service 2 -> MongoDB: Retrieve CIDs
    MongoDB -> Service 2: Return CIDs
    Service 2 -> Client: Return CIDs

    Kafka -> Service 2: CID Message
    Service 2 -> MongoDB: Store CID
    
![Untitled(14)](https://github.com/nabil-Tounarti/iExce/assets/117689544/5b6c12fb-f94c-4452-9de7-76c9bf6bcae7)


### Front End
![image](https://github.com/nabil-Tounarti/iExce/assets/117689544/f50bbfe5-2a6b-44bb-9d7a-deafb1142bac)

![image](https://github.com/nabil-Tounarti/iExce/assets/117689544/cc01cb34-75e4-48e3-97b9-7e353a360161)

![image](https://github.com/nabil-Tounarti/iExce/assets/117689544/ddce22f2-a0e4-4f12-b149-6db363dd5a56)

![image](https://github.com/nabil-Tounarti/iExce/assets/117689544/82b34793-8f00-45d8-9f4c-fcc391d74034)

![image](https://github.com/nabil-Tounarti/iExce/assets/117689544/1a0f3973-91be-4932-b110-697077710264)







