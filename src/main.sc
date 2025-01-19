sc
     require: plants.csv
     require: order.sc

theme: /

state: Main
    q: (.*)
    script:
        var query = $request.query;
        var results = [];
        for (var i = 0; i < plants.length; i++) \{
            var plant = plants[i];
            if (query.includes(plant.name) || query.includes(plant.attributes.type) || query.includes(plant.attributes.care)) \{
                results.push(plant);
            \}
        \}
        if (results.length > 0) \{
            $reactions.answer("Мы нашли следующие растения для вас:");
            for (var j = 0; j < results.length; j++) \{
                var result = results[j];
                $reactions.answer(result.name + " - " + result.attributes.price + " руб.");
            \}
        \} else \{
            $reactions.answer("К сожалению, мы не нашли подходящих растений.");
        \}
    buttons:
        "Корзина" -> /Cart