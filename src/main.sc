theme: /

state: Приветствие
  q!: $regex</start>
  a: Здравствуйте! Чем могу помочь?
  go: /Обработка_первого_ответа
  buttons:
    - "Наш сайт: https://elovpark.ru/"
    - "Корзина"
  intent: /sys/aimylogic/ru/parting
  event: noMatch || go: /Завершение

state: Завершение
  a: До свидания! Обращайтесь еще.
  script: $reactions.finish("bye")

state: Обработка_первого_ответа
  q!: * # Ответ пользователя
  script:
    var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
    // Определяем параметры растения по запросу
    $session.selectedSize = userInput.match(/большой|средний|маленький/i) ? userInput.match(/большой|средний|маленький/i)[0] : null;
    $session.selectedType = userInput.match(/дерево|цветок/i) ? userInput.match(/дерево|цветок/i)[0] : null;
    $session.myResult = "Ответьте на пару наших вопросов, и мы подберем растение!"
  a: {{ $session.myResult }}
  go: /Уточнение_цвета
  event: noMatch || go: /Обработка_первого_ответа

state: Уточнение_цвета
  a: Какой цвет растения вы бы хотели? Например, зеленый, белый, красный и т.д.
  q!: * # Пользовательский текст
  script:
    var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
    var colorMatch = userInput.match(/зеленый|белый|красный|синий|желтый/i);

    if (colorMatch) {
      $session.selectedColor = colorMatch[0];
      $session.myResult = "Вы выбрали цвет: " + $session.selectedColor + ".";
      $session.retryColor = false;
    } else {
      $session.myResult = "Я не распознал цвет. Пожалуйста, укажите цвет растения.";
      $session.retryColor = true; // Флаг, что цвет не распознан
    }
  a: {{ $session.myResult }}
  go: $session.retryColor ? /Уточнение_цвета : /Предложение
  event: noMatch || go: /Уточнение_цвета

state: Предложение
  script:
    // Список всех доступных растений
    var plants = [
      { name: "Фикус", color: "зеленый", size: "большой", type: "дерево", care: "легкий" },
      { name: "Монстера", color: "зеленый", size: "большой", type: "дерево", care: "средний" },
      { name: "Спатифиллум", color: "белый", size: "средний", type: "цветок", care: "легкий" },
      { name: "Антуриум", color: "красный", size: "средний", type: "цветок", care: "средний" },
      { name: "Гортензия", color: "синий", size: "средний", type: "цветок", care: "средний" },
      { name: "Каландивия", color: "желтый", size: "маленький", type: "цветок", care: "легкий" }
    ];

    // Фильтрация растений по цвету, размеру и типу
    var filteredPlants = plants.filter(function(plant) {
      return (!$session.selectedColor || plant.color === $session.selectedColor) &&
             (!$session.selectedSize || plant.size === $session.selectedSize) &&
             (!$session.selectedType || plant.type === $session.selectedType);
    });

    if (filteredPlants.length > 0) {
      var plantNames = filteredPlants.map(function(plant) {
        return plant.name;
      }).join(", ");
      $session.myResult = "Мы можем предложить вам следующие растения: " + plantNames + ". Хотите добавить их в корзину?";
      $session.suggestedPlants = filteredPlants; // Сохраняем предложенные растения в сессии
    } else {
      $session.myResult = "К сожалению, нет доступных растений с такими параметрами.";
    }
  a: {{ $session.myResult }}
  go: /Корзина
  event: noMatch || go: /Корзина

state: Корзина
  a: Что вы хотите сделать с выбранными растениями? Добавить в корзину, посмотреть корзину или продолжить выбор?
  q!: * # Ответ пользователя
  script:
    var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
    if (userInput.includes('добавить')) {
      if ($session.suggestedPlants && $session.suggestedPlants.length > 0) {
        $session.cart = $session.cart || [];
        $session.cart = $session.cart.concat($session.suggestedPlants);
        $session.myResult = "Растения добавлены в корзину.";
        delete $session.suggestedPlants; // Очищаем предложенные растения
      } else {
        $session.myResult = "Нет растений для добавления в корзину.";
      }
      toState("./");
    } else if (userInput.includes('посмотреть')) {
      if ($session.cart && $session.cart.length > 0) {
        var cartItems = $session.cart.map(function(plant) {
          return plant.name;
        }).join(", ");
        $session.myResult = "В вашей корзине: " + cartItems + ". Что вы хотите сделать дальше?";
      } else {
        $session.myResult = "Ваша корзина пуста.";
      }
      toState("./");
    } else if (userInput.includes('продолжить')) {
      $session.myResult = "Давайте продолжим выбор.";
      go("/Обработка_первого_ответа");
    } else {
      $session.myResult = "Пожалуйста, уточните, хотите ли вы добавить растения в корзину, посмотреть корзину или продолжить выбор.";
      toState("./");
    }
  a: {{ $session.myResult }}
  event: noMatch || toState = "/"
