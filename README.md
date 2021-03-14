# Игра To The Void

Выпускной проект 2015 года IT школы Samsung.

![logo game to the void](https://github.com/tonykolomeytsev/to-the-void/raw/master/screenshots/ill_github_header.png)

![](https://github.com/tonykolomeytsev/to-the-void/raw/master/screenshots/screens.png)

Проект был написан в 2015 году на Java 6, допиливался по каким-то причинам в 2016 году. 

В 2021 году был найден спустя много лет, отполирован, адаптирован под свежие версии Android.

### Особенности проекта

1. Геометрически верный алгоритм расчета коллизий между треугольной ракетой игрока и многоугольными астероидами.

2. Астероиды полностью процедурно-генерируемые.

3. Для рисования графики используются исключительно возможности `android.graphics.Canvas` и `android.view.SurfaceView`.

Самая главная особенность - это каким-то чудом написано мною в 17 лет.

### Что было изменено в проекте для публикации на GitHub

- Иконка приложения заменена на новую, векторную.
- Проведена чистка кода, где это было возможно.
- С помощью профайлера были устранены утечки памяти.
- В целом выпилен неэффективный код и повышена производительность рендеринга.
- Удален код связанный с сервисами Google Play Games.
- Добавлен эффект космического мерцания, который я не успел сделать 6 лет назад)

### Сборка проекта

Для сборки проекта не треуется особых ключей и доступов. Просто клонируйте проект, убедитесь что у вас установлены JDK 1.8 или JDK 11 и все будет хорошо.