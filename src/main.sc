theme: /
  state: Приветствие
    q!: $regex</start>
    random:
      a: Здравствуйте! Чем могу помочь?
    buttons:
      "Выбрать растение" -> /Выбор_растения
      "Корзина" -> /Корзина

  state: Выбор_растения
    q!: *
    a: Отлично! Напишите, что вы ищете: "цветок" или "дерево".
    buttons:
      "Назад" -> /Приветствие

  state: Выбор_по_типу
    q!: *  # Пользовательский ввод
    script:
      var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
      var plants = [
        {
          "name": "Фикус",
          "color": "зеленый",
          "size": "большой",
          "type": "дерево",
          "care": "легкий"
        },
        {
          "name": "Монстера",
          "color": "зеленый",
          "size": "большой",
          "type": "дерево",
          "care": "средний"
        },
        {
          "name": "Спатифиллум",
          "color": "белый",
          "size": "средний",
          "type": "цветок",
          "care": "легкий"
        },
        {
          "name": "Антуриум",
          "color": "красный",
          "size": "средний",
          "type": "цветок",
          "care": "средний"
        },
        {
          "name": "Гортензия",
          "color": "синий",
          "size": "средний",
          "type": "цветок",
          "care": "средний"
        },
        {
          "name": "Каландивия",
          "color": "желтый",
          "size": "маленький",
          "type": "цветок",
          "care": "легкий"
        }
      ];

      if (userInput.includes('цветок')) {
        // Фильтруем по типу "цветок"
        var filteredPlants = plants.filter(function(plant) {
          return plant.type === "цветок";
        });
        var result = filteredPlants.map(function(plant) {
          return plant.name;
        }).join(", ");
        $session.myResult = "Вот растения типа 'Цветок': " + result;
        return { toState: "/Результат" };
      } else if (userInput.includes('дерево')) {
        // Фильтруем по типу "дерево"
        var filteredPlants = plants.filter(function(plant) {
          return plant.type === "дерево";
        });
        var result = filteredPlants.map(function(plant) {
          return plant.name;
        }).join(", ");
        $session.myResult = "Вот растения типа 'Дерево': " + result;
        return { toState: "/Результат" };
      } else {
        $session.myResult = "Пожалуйста, укажите тип растения: 'цветок' или 'дерево'.";
        return { toState: "/Выбор_растения" };
      }

  state: Результат
    q!: *
    a: {{ $session.myResult }}  # Выводим результат, сохранённый в сессии
    buttons:
      "Назад" -> /Приветствие
      "Выбрать другое" -> /Выбор_растения
