theme: /

state: Приветствие
    q!: $regex</start>
    random: 
        a: Добрый день! Чем могу помочь?
        a: Приветствую! Готов помочь вам с выбором растения.
    buttons:
        {text: "Наш сайт", url: "https://elovpark.ru/"}
        "Выбрать растение" -> /Поиск растения
    intent: /sys/aimylogic/ru/parting || toState = "/Проверка"
    event: noMatch || toState = "./"

state: Поиск растения
    q!: * # Пользовательский текст
    script:
        if (!$parseTree || !$parseTree.text) {
            $session.myResult = "К сожалению, я не смог обработать ваш запрос. Попробуйте сформулировать его по-другому.";
        } else {
            // Получаем текст пользователя
            var userInput = $parseTree.text;

            // Определяем цвет и размер
            var colorMatch = userInput.match(/Green|Белый|Красный|Синий|Желтый/i);
            var sizeMatch = userInput.match(/Большой|Средний|Маленький/i);

            // Пример: Логика фильтрации (подставьте свою логику работы с базой данных)
            var results = [];
            if (colorMatch && sizeMatch) {
                if (sizeMatch[0] === 'Большой') {
                    if (colorMatch[0] === 'Green') {
                        results = ['Фикус', 'Монстера'];
                    } else if (colorMatch[0] === 'Белый') {
                        results = ['Спатифиллум'];
                    }
                } else if (sizeMatch[0] === 'Средний') {
                    if (colorMatch[0] === 'Красный') {
                        results = ['Антуриум'];
                    } else if (colorMatch[0] === 'Синий') {
                        results = ['Гортензия'];
                    }
                } else if (sizeMatch[0] === 'Маленький') {
                    if (colorMatch[0] === 'Желтый') {
                        results = ['Каландивия'];
                    }
                }
            }

            if (results.length > 0) {
                $session.myResult = results.join(", ");
            } else {
                $session.myResult = "Подходящие варианты не найдены.";
            }
        }
    a: Вот что я нашел по вашему запросу: {{ $session.myResult }}
    buttons:
        "Выбрать другое растение" -> /Фильтры
        "На главную" -> /Приветствие
    intent: /Оформление заказа || toState = "/Оформление заказа"
    event: noMatch || toState = "./"    

state: Не понял
    event!: noMatch
    a: Простите, я не совсем понял. Попробуйте задать вопрос по-другому.
