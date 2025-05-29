🏢 Contextualització de l’organització client

L’organització client és una entitat local de Vilanova i la Geltrú amb un fort compromís amb el medi ambient i la sostenibilitat. Té com a objectiu fomentar hàbits de mobilitat saludable i responsables entre la ciutadania, tot promovent l’ús de la bicicleta com a eina educativa i de conscienciació cívica i ecològica.

🎯 Objectiu de l’aplicació

L’objectiu principal de l’aplicació és promoure l’educació mediambiental i cívica mitjançant l’ús de la bicicleta, incentivant la participació ciutadana amb un sistema de punts i recompenses que afavoreixi el comerç local i els hàbits sostenibles.

💡 Principals funcionalitats
🔐 Registre i gestió d’usuaris
    Registre limitat a residents o treballadors habituals de Vilanova i zones properes.

    El registre ha de ser validat per un administrador després d’una entrevista via Meet.

    Sistema d’activació/desactivació i recuperació de contrasenya.

🚴 Seguiment de rutes i sistema de punts
    Els ciclistes poden iniciar, pausar i finalitzar rutes des del mòbil.

    El sistema calcula automàticament els quilòmetres i assigna punts.

    Rutes validades per un administrador segons criteris de velocitat i trajecte.

🎁 Sistema de recompenses

    Recompenses variades reservables des de l’app.

    Cada usuari pot obtenir una recompensa reservada al mateix temps.

    La validació i recollida de recompenses és responsabilitat de l’usuari.

📱 Comunicació amb els usuaris

    Totes les notificacions i comunicacions es fan mitjançant l’app mòbil.

📊 Gestió per part de l’administrador (frontend web)

    Validació de rutes i usuaris.

    Creació i gestió de recompenses i punts de bescanvi.

    Visualització i filtratge de dades (rutes, usuaris, recompenses).

☁️ Desplegament i disponibilitat

    El sistema es desplega en contenidors Docker amb backend i base de dades en el mateix servidor, accessible 24/7.


🛠️ Resum de Tecnologies Utilitzades

El desenvolupament del projecte s'ha realitzat utilitzant un conjunt de tecnologies modernes, adaptades a les necessitats d'una aplicació multiplataforma amb arquitectura client-servidor:
🔙 Backend

    Java: Llenguatge principal per a la lògica de negoci i serveis.

    Spring Boot: Framework per al desenvolupament d’API REST, permetent una arquitectura modular, escalable i fàcil de mantenir.

    Hibernate (JPA): Eina per a la persistència de dades, utilitzada per gestionar la comunicació amb la base de dades de manera eficient mitjançant ORM.

    API REST: Comunicació entre el backend i els clients (web i mòbil) basada en serveis RESTful desacoblats.

📱 Frontend mòbil

    Kotlin: Llenguatge de programació utilitzat per desenvolupar una app nativa per a dispositius Android.

    Jetpack Compose: Toolkit modern de Google per crear interfícies d’usuari declaratives i reactives.

    Arquitectura MVVM + Clean Architecture: Patrons d’arquitectura per garantir una estructura clara, mantenible i escalable.


🌐 Frontend web (Administrador)

    HTML + CSS + Thymeleaf: Tecnologies utilitzades per crear una interfície web funcional, responsiva i amigable per a la gestió administrativa.

🐳 Infraestructura i desplegament

    Docker: Utilitzat per contenitzar i desplegar tant el backend com la base de dades, garantint portabilitat i consistència en diferents entorns.

🔗 **Enllaços**

[Enllaç video](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/VideoP4Entrbicis.mov)

[Enllaç Documentació](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/index.adoc)

Pantalles d'exemple:

🟦 Login Web

Pantalla d'inici de sessió de l'aplicació web, on els usuaris poden autenticar-se amb les seves credencials per accedir al sistema.

![Login Web](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/LoginWeb.png)

📱 Login Android

Versió per a mòbil Android de la pantalla de login, adaptada per a dispositius amb pantalla tàctil.

![Login Android](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/Login.png)

🎁 Llista d’exemple de recompenses

Visualització de les recompenses disponibles per als usuaris, amb opcions per reclamar o reservar.

![Lista Ejemplo Recompensas](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/PantallaRecompensa.png)

⚙️ Paràmetres del sistema

Pantalla d’administració on es configuren els paràmetres generals del sistema com puntuacions, nivells i valors de recompensa.

![Parametros del sistema](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/ParametrosSistema.png)

🧭 Llista de rutes (Android)
Mostra totes les rutes disponibles dins l'aplicació Android, amb informació bàsica com la distància i el nivell de dificultat.

![Pantalla lista Ruta Android](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/PantallaRutaAndr.png)

🌍 Detalls ruta (Web)

Visualització detallada d’una ruta específica en la versió web, amb informació estesa i mapa.

![Pantalla Detalls Ruta Web](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/PantallaRutaWeb.png)

🎫 Reclamar recompensa (Android)

Interfície d’usuari per a reclamar una recompensa després d’haver completat una ruta.

![Pantalla Reclamar Recompensa Android](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/ReclamarRecompensas.png)

🚲 Visualització de ruta (Android)

Mostra el traçat d’una ruta concreta, permetent a l’usuari seguir el recorregut amb el GPS.

![Pantalla Ruta Android](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/RutaAndroid.png)

📌 Reservar recompensa

Opció per reservar una recompensa concreta abans de completar els requisits, per assegurar-ne la disponibilitat.

![Pantalla Reservar Recompensa](https://github.com/mayoadria/Projecte-Final-EntreBicis/blob/main/FotosGithub/ReservarRecompensa.png)
