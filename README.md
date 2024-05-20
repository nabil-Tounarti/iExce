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

### Pourquoi j'ai choisi Go plutôt que Java ou Rust pour ce projet :
Pourquoi j'ai opté pour Go plutôt que Java ou Rust pour ce projet ? 
regardons un peu plus en détail ce qu'on cherchait à faire. Notre projet implique la gestion de fichiers à travers des services interconnectés utilisant IPFS et MongoDB. L'idée était de créer deux services distincts : un pour charger des documents sur IPFS et notifier l'autre service, et l'autre pour recevoir ces notifications, stocker les identifiants des nouveaux fichiers dans MongoDB, et offrir une interface pour consulter ces identifiants.

Maintenant, pourquoi Go ? D'abord, on voulait quelque chose qui soit à la fois performant et facile à gérer pour nos microservices. Go est parfait pour ça. Avec son modèle de concurrence intégré, il peut gérer efficacement plusieurs tâches en même temps, ce qui est crucial pour notre application qui traite des fichiers. De plus, les binaires Go sont compilés de manière statique, donc pas besoin de se soucier des dépendances. C'est super pour déployer nos services sans tracas.

En ce qui concerne Java, oui, c'est un choix solide pour les grosses applications d'entreprise, mais pour notre cas, ça semblait un peu trop lourd. On avait pas besoin de tout le kit et caboodle que Java offre, et ça aurait pu rendre nos microservices un peu trop gourmands en ressources.

Quant à Rust, c'est vrai que sa sécurité mémoire et ses performances sont très attrayantes, mais honnêtement, on n'avait pas forcément besoin de toute cette puissance pour notre projet.

### Ce que j'aurais pu faire mieux dans ma solution :

    Scalabilité : J'aurais dû penser plus à comment notre solution peut évoluer si on a plus de trafic. Peut-être qu'on aurait dû envisager Kubernetes ou un truc du genre.
    Sécurité : On aurait pu renforcer un peu plus la sécurité, surtout quand on manipule des données sensibles comme les fichiers et les notifications. Ça aurait été bien d'ajouter plus de vérifications.
    Gestion des erreurs : On aurait pu être plus malins sur la gestion des erreurs, histoire de rendre nos messages d'erreur plus clairs et de mieux gérer les moments où ça part en vrille.
    Tests : Plus de tests auraient été bienvenus, histoire de s'assurer que tout fonctionne comme prévu. On aurait pu automatiser ça pour gagner du temps.
    Documentation : La doc aurait pu être plus détaillée, avec des exemples concrets et des instructions plus claires. Ça aurait facilité la vie de tout le monde.







