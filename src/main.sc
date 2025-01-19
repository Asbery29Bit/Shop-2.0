theme: /

    state: Приветствие
        q!: $regex</start>
        a: Здравствуйте! Чем могу помочь?
        go: /Обработка ответа
        buttons:
            - { text: "Наш сайт", url: "https://elovpark.ru/" }
            - { text: "Корзина", action: "/Корзина" }
        intent: /sys/aimylogic/ru/parting || toState = "/Проверка"
        event: noMatch || toState = "/Обработка ответа"

    state: Обработка ответа
        q!: * # Ответ пользователя
        script:
            var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
            // Определяем параметры растения по запросу
            $session.selectedSize = userInput.match(/большой|средний|маленький/i) ? userInput.match(/большой|средний|маленький/i)[0] : '';
            $session.selectedType = userInput.match(/дерево|цветок/i) ? userInput.match(/дерево|цветок/i)[0] : '';
            $session.myResult = "Ответьте на пару наших вопросов, и мы подберем растение!";
        a: {{ $session.myResult }}
        go: /Уточнение цвета
        event: noMatch || toState = "./"
    
    state: Уточнение цвета
        a: Какой цвет растения вы бы хотели? Например, зеленый, белый, красный и т.д.
        q!: * # Пользовательский текст
        script:
            var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
            var colorMatch = userInput.match(/зеленый|белый|красный|синий|желтый/i);
            
            if (colorMatch) {
                $session.selectedColor = colorMatch[0];
                $session.myResult = "Вы выбрали цвет: " + $session.selectedColor + ".";
            } else {
                $session.myResult = "Я не распознал цвет. Пожалуйста, укажите цвет растения.";
            }
        a: {{ $session.myResult }}
        go: /Предложение
        event: noMatch || toState = "/Предложение"
    
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
                return plant.color === $session.selectedColor && plant.size === $session.selectedSize && plant.type === $session.selectedType;
            });

            if (filteredPlants.length > 0) {
                var plantNames = filteredPlants.map(function(plant) {
                    return plant.name;
                }).join(", ");
                $session.myResult = "Мы можем предложить вам следующие растения: " + plantNames + ". Хотите добавить их в корзину?";
                $session.selectedPlants = filteredPlants; // Сохраняем выбранные растения в сессии
            } else {
                $session.myResult = "К сожалению, нет доступных растений с такими параметрами.";
            }
        a: {{ $session.myResult }}
        go: /Корзина
        event: noMatch || toState = "/Корзина"

    state: Корзина
        a: Что вы хотите сделать с выбранными растениями? Добавить в корзину или продолжить выбор?
        q!: * # Ответ пользователя
        script:
            // Логика корзины и дальнейших действий
        a: {{ $session.myResult }}
