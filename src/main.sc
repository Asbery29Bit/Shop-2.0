theme: /

    state: Приветствие
        q!: $regex</start>
        a: Здравствуйте! Чем могу помочь?
        go: /Обработка ответа
        buttons:
            {text: "Наш сайт", url: "https://elovpark.ru/"}
            "Корзина" -> /Корзина
        intent: /sys/aimylogic/ru/parting || toState = "/Проверка"
        event: noMatch || toState = "/Обработка ответа"
    
    state: Обработка ответа
        q!: * # Ответ пользователя
        script:
            var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
            // Определяем, для кого нужны цветы
            $session.recipient = userInput.match(/бабу*|сын*|внучк*|самого себя|себе/i) ? userInput.match(/бабушки|сын|внучка|самого себя|себе|бабушка|внучке|сыну|самому себе/i)[0] : "неизвестному получателю";
            $session.myResult = "Ответьте на пару наших вопросов и мы подберем растение для " + $session.recipient + ".";
        a: {{ $session.myResult }}
        a: Какой цвет растения вы бы хотели?
        go: /Уточнение цвета
        event: noMatch || toState = "./"
    

        
        
        state: Уточнение цвета
            intent!: /Уточнение цвета
            script:
                $session.color = $parseTree.Цвет;
            if: $session.color == undefined
                a: Я не понял. Вы сказали: {{$request.query}}
                go!: /Уточнение цвета
            else: 
                a: ваш цвет {{$session.color}}
                go!: /Уточнение размера
            event: noMatch || toState = "./"
        
        
        
        
    
    state: Уточнение размера
        q!: * # Пользовательский текст
        script:
            var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
            if (!userInput) {
                $session.myResult = "Пожалуйста, укажите размер растения.";
                return { toState: "/Уточнение размера" }; // Повторяем вопрос
            } else {
                var sizeMatch = userInput.match(/большой|средний|маленький/i);
                
                if (sizeMatch) {
                       $session.selectedSize = sizeMatch[0];
                       $session.myResult = "Вы выбрали размер: " + $session.selectedSize + ".";
                   } else {
                       $session.myResult = "Я не распознал размер. Пожалуйста, укажите один из следующих размеров: маленький, средний, большой.";
                       return { toState: "/Уточнение размера" };
                   }
            }
        a: {{ $session.myResult }}
        a: Какой тип растения вы бы хотели?
        go: /Уточнение типа
        event: noMatch || toState = "./"
    
    state: Уточнение типа
        q!: * # Пользовательский текст
        script:
            var userInput = $parseTree.text ? $parseTree.text.toLowerCase() : '';
            if (!userInput) {
                $session.myResult = "Пожалуйста, укажите тип растения.";
                return { toState: "/Уточнение типа" }; // Повторяем вопрос
            } else {
                var typeMatch = userInput.match(/цветок|кустарник|дерево/i);
                
                if (typeMatch) {
                       $session.selectedType = typeMatch[0];
                       $session.myResult = "Вы выбрали тип: " + $session.selectedType + ".";
                   } else {
                       $session.myResult = "Я не распознал тип. Пожалуйста, укажите один из следующих типов: цветок, кустарник или дерево.";
                       return { toState: "/Уточнение типа" };
                   }
            }
        a: {{ $session.myResult }}
        a: Подбираем подходящие варианты...
        go!: /Подбор растений
        event: noMatch || toState = "./"
    
    state: Подбор растений
        script:
            var plants = [
                { name: "Роза", color: "красный", size: "маленький", type: "цветок", link:"-"},
                { name: "Либерти", color: "зеленый", size: "средний", type: "кустарник", link:"https://elovpark.ru/product/%d1%85%d0%be%d1%81%d1%82%d0%b0-%d0%bb%d0%b8%d0%b1%d0%b5%d1%80%d1%82%d0%b8/"},
                { name: "Вербейник", color: "желтый", size: "средний", type: "цветок", link:"https://elovpark.ru/product/%d0%b2%d0%b5%d1%80%d0%b1%d0%b5%d0%b9%d0%bd%d0%b8%d0%ba-%d1%82%d0%be%d1%87%d0%b5%d1%87%d0%bd%d1%8b%d0%b9/" },
                { name: "Тюльпан", color: "желтый", size: "средний", type: "цветок", link:"-" },
                { name: "Барбарис", color: "красный", size: "большой", type: "кустарник", link:"https://elovpark.ru/product/%d0%b1%d0%b0%d1%80%d0%b1%d0%b0%d1%80%d0%b8%d1%81-%d1%82%d1%83%d0%bd%d0%b1%d0%b5%d1%80%d0%b3%d0%b0-%d0%b0%d1%82%d1%80%d0%be%d0%bf%d1%83%d1%80%d0%bf%d1%83%d1%80%d0%b5%d0%b0/"},
                { name: "Бадан", color: "розовый", size: "маленький", type: "цветок", link:"https://elovpark.ru/product/%d0%b1%d0%b0%d0%b4%d0%b0%d0%bd-%d1%82%d0%be%d0%bb%d1%81%d1%82%d0%be%d0%bb%d0%b8%d1%81%d1%82%d0%bd%d1%8b%d0%b9/" },
                { name: "Кактус", color: "зеленый", size: "маленький", type: "цветок", link:"-" },
                { name: "Орхидея", color: "белый", size: "маленький", type: "цветок", link:"-" },
                { name: "Медуница", color: "синий", size: "маленький", type: "цветок", link:"https://elovpark.ru/product/%d0%bc%d0%b5%d0%b4%d1%83%d0%bd%d0%b8%d1%86%d0%b0-%d1%81%d0%b0%d1%85%d0%b0%d1%80%d0%bd%d0%b0%d1%8f-%d0%bc%d0%b8%d1%81%d1%81%d0%b8%d1%81-%d0%bc%d1%83%d0%bd/" },
                { name: "Пион", color: "красный", size: "маленький", type: "цветок", link:"https://elovpark.ru/product/%d0%bf%d0%b8%d0%be%d0%bd-%d1%82%d0%be%d0%bd%d0%ba%d0%be%d0%bb%d0%b8%d1%81%d1%82%d0%bd%d1%8b%d0%b9/" },
                { name: "Ирис Вайт Ледис", color: "белый", size: "средний", type: "цветок", link:"https://elovpark.ru/product/%d0%b8%d1%80%d0%b8%d1%81-%d0%b2%d0%b0%d0%b9%d1%82-%d0%bb%d0%b5%d0%b4%d0%b8%d1%81/" },
                { name: "Астра", color: "красный", size: "средний", type: "цветок", link:"-" },
                { name: "Бегония", color: "розовый", size: "маленький", type: "цветок", link:"-" },
                { name: "Каллы", color: "белый", size: "средний", type: "цветок", link:"-" },
                { name: "Пальма", color: "зеленый", size: "большой", type: "дерево", link:"-" },
                { name: "Нарцисс", color: "желтый", size: "маленький", type: "цветок", link:"-" },
                { name: "Фиалка", color: "синий", size: "маленький", type: "цветок", link:"-" },
                { name: "Гладиолус", color: "красный", size: "большой", type: "цветок", link:"-" },
                { name: "Мирт", color: "зеленый", size: "маленький", type: "цветок", link:"-" },
                { name: "Цинерария", color: "синий", size: "средний", type: "цветок", link:"-" },
                { name: "Клематис", color: "белый", size: "большой", type: "цветок", link:"-" },
                { name: "Лаванда", color: "синий", size: "средний", type: "цветок", link:"-" }
            ];
    
            var matches = plants.filter(function(plant) {
                return plant.color === $session.selectedColor &&
                       plant.size === $session.selectedSize &&
                       plant.type === $session.selectedType;
            });
    
            if (matches.length > 0) {
                $session.myResult = "Мы нашли подходящие варианты: " + "\n" + matches.map(function(plant) { return plant.name + " (ссылка: " + plant.link + ")"; }).join("\n") + ".";
            } else {
                var randomPlant = plants[Math.floor(Math.random() * plants.length)];
                $session.myResult += " К сожалению, мы не нашли растений, соответствующих вашим параметрам, но мы можем предложить вам: " + randomPlant.name + " (ссылка: " + randomPlant.link + ")" + ".";
            }
        a: {{ $session.myResult }}
        a: Спасибо за ваш выбор!
        event: noMatch || toState = "./"
        buttons:
            "Вернутся в начало" -> /Приветствие
