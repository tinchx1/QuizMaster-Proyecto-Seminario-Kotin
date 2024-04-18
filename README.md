**QuizMaster**

**Introducción**

QuizMaster es un juego de preguntas y respuestas diseñado para desafiar el conocimiento del jugador en una variedad de temas. Cada ronda del juego presenta una serie de preguntas de opción múltiple, donde el jugador debe seleccionar la respuesta correcta entre varias opciones. El objetivo es acumular la mayor cantidad de puntos posible al responder correctamente a las preguntas.

**Trabajo a entregar**

La primera entrega de este trabajo consiste en construir las bases para el desarrollo del juego. Para ello será necesario definir dos actividades -activity-

- Primera: la interfaz está compuesta principalmente por una lista de categorías.
- Segunda: contiene la pregunta con las 4 opciones de respuesta. Además se muestra en la parte superior la cantidad de aciertos contra la cantidad de errores dentro de esa categoría.

**Pautas de corrección**

La entrega se realizará en grupos de a dos personas, el cual debe mantenerse a lo largo de las restantes entregas.

El desarrollo será realizado utilizando el IDE Android Studio y el lenguaje de programación **Kotlin**.

Los puntos de la entrega que serán evaluados son los siguientes:

- **Layout:** Se deberá construir dos actividades:
- Primera: Lista de las categorías existentes, cada ítem debe ser un botón. Esta actividad se debe implementar con scroll
- Segunda: Se muestra en la parte superior la categoría, el puntaje, el botón de comodín para cambiar de pregunta y el tiempo (no es necesario implementarlo para esta entrega, solo dejar el texto). A continuación se muestra la pregunta con sus 4 opciones. Al seleccionar la respuesta:
- si es correcta se muestra en verde.
- Si es incorrecta se muestra en rojo, y en un color neutro (ejemplo gris) se muestra la respuesta correcta.

Se muestra el resultado durante unos segundos y se pasa a la siguiente pregunta. Cuando se finaliza la ronda vuelve a la pantalla principal

- **Juego**: Una vez que el jugador elige una categoría, comienza la ronda de preguntas. Se presentan varias preguntas de opción múltiple, una por una, con un temporizador que indica el tiempo restante para responder (no es necesario para esta entrega).

  Opciones de respuesta: Para cada pregunta, se muestran 4 opciones de respuesta. El jugador debe tocar la opción que considera correcta antes de que se agote el tiempo.

- Respuesta correcta/incorrecta: Después de que el jugador selecciona una respuesta, se muestra si la respuesta es correcta o incorrecta. En caso de respuesta incorrecta, se muestra la respuesta correcta.

La partida termina después de que se responden todas las preguntas en la ronda. Se muestra la puntuación final del jugador y la opción para jugar de nuevo.

- **Puntuación**: Si la repuestos es correcta suma 1. El puntaje se forma de total de preguntas que se respondieron correctamente / total de preguntas que pasaron .
- **Comodín**: Al presionar el comodín se cambia la pregunta y se deshabilita el comodín.

**Entrega**

**La fecha límite para realizar la entrega es el 8/5/2024.**

La entrega deberá ser enviada utilizando la plataforma IDEAS indicando los integrantes del grupo. Los grupos es de a lo sumo 2 personas o de forma individual.

**Material complementario**

A continuación se detalla un ejemplo para leer un archivo y crear JsonObject. Los archivos deben estar en app/src/assets.

Para Question se utilizó una clase Kotlin que tiene las variables de instancia necesarias para modelar las preguntas con sus opciones y la respuesta correcta.

Se carga además en ideas un TXT con los distintos JSON por categoría.
