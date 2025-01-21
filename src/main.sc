theme: /
    
    state: Приветствие
        script:
            $session.color = null
            $session.size = null
            $session.type = null
        q!: $regex</start>
        a: Здравствуйте! Чем могу помочь?
        buttons:
            {text: "Наш сайт", url: "https://elovpark.ru/"}
            "Корзина" -> /Корзина
        go: /Оформление заказа
        intent: /sys/aimylogic/ru/parting || toState = "/Проверка"
        event: noMatch || toState = "/Обработка ответа"
        
    state: Оформление заказа
        intent!: /Оформление заказа
        script:
            $session.recipient = $parseTree._recipient;
        if: $session.recipient == undefined
            a: Ответьте на пару наших вопросов, и мы подберем растение для неизвестного получателя.
            a: Какого цвета растение вы бы хотели?
            go: /Уточнение цвета
        else: 
            a: Ответьте на пару наших вопросов, и мы подберем растение для {{$session.recipient}}
            a: Какого цвета растение вы бы хотели?
            go: /Уточнение цвета
        event: noMatch || toState = "./"
        
    state: Уточнение цвета
        intent!: /Уточнение цвета
        script:
            $session.color = $parseTree._color.date;
        if: $session.color == undefined
            a: Вы сказали: "{{$request.query}}". Но растения такого цвета я не знаю, пожалуйста, укажите другой
            #go: /Уточнение цвета
        else: 
            a: вы выбрали цвет {{$session.color}}
            a: Какого размера растение вы бы хотели?
            go: /Уточнение размера
        buttons:
            "Не указывать" -> /Уточнение размера
        event: noMatch || toState = "./"
        
        
    
    state: Уточнение размера
        intent!: /Уточнение размера
        script:
            $session.size = $parseTree._size;
        if: $session.size == undefined
            a: Вы сказали: "{{$request.query}}". Но растения такого размера я не знаю, пожалуйста, укажите другой
            #go: /Уточнение размера
        else: 
            a: вы выбрали размер {{$session.size}}
            a: Какой тип растения вы бы хотели?
            go: /Уточнение типа
        buttons:
            "Не указывать" -> /Уточнение типа
        event: noMatch || toState = "./"
        
        
    
    state: Уточнение типа
        intent!: /Уточнение типа
        script:
            $session.type = $parseTree._type;
        if: $session.type == undefined
            a: Вы сказали: "{{$request.query}}". Но растения такого типа я не знаю, пожалуйста, укажите другой
            #go: /Уточнение типа
        else: 
            a: вы выбрали тип {{$session.type}}
            go!: /Проверка
        buttons:
            "Не указывать" -> /Подбор растений
        event: noMatch || toState = "./"
        
        
    
    state: Проверка
        a: Вы задали следующие параметры: {{$session.color}}, {{$session.size}}, {{$session.type}}
        a: Все верно?
        event: noMatch || toState = "./"
        
    state: Согласие
        intent: /Согласие
        go!: /Подбор растений
            
    
    state: Подбор растений
        intent!: /Подбор растений
        a: Подбираем подходящие варианты...
        script:
            $temp.response = $http.post(
                "http://185.242.118.144:8000/find_plants", 
                {
                    body: {
                        color: $session.color,
                        size: $session.size,
                        type: $session.type
                    },
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
            );
        # Отправляем запрос на внешний API для поиска растений
        if: $temp.response.isOk && $temp.response.data.results.length > 0
            script:
                $temp.plantMessages = "";
                $temp.index = 0;
                while ($temp.index < $temp.response.data.results.length) {
                    $temp.plantMessages += "---\n";
                    $temp.plantMessages += "Название: " + $temp.response.data.results[$temp.index].name + "\n";
                    $temp.plantMessages += "Цвет: " + $temp.response.data.results[$temp.index].color + "\n";
                    $temp.plantMessages += "Размер: " + $temp.response.data.results[$temp.index].size + "\n";
                    $temp.plantMessages += "Тип: " + $temp.response.data.results[$temp.index].type + "\n";
                    $temp.plantMessages += "Ссылка: " + $temp.response.data.results[$temp.index].link + "\n";
                    $temp.index++;
                }
            a: |
                Вот несколько растений, которые мы нашли:
                {{$temp.plantMessages}}
        else: 
            # Если растения не найдены или произошла ошибка
            a: Не удалось найти растения по вашим параметрам. Попробуй ещё раз.
        buttons:
            "Вернутся в начало" -> /Приветствие