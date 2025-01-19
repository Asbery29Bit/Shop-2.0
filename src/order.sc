```sc
require: function.js

state: Cart
    intent!: /корзина
    a: Ваша корзина:
    script:
        $temp.totalSum = 0;
        for (var i = 0; i < $session.cart.length; i++) \{
            var item = $session.cart i;
            $reactions.answer(item.name + " - " + item.price + " руб.");
            $temp.totalSum += item.price;
        \}
        $reactions.answer("Общая сумма: " + $temp.totalSum + " руб.");
    buttons:
        \{text: "Оформить заказ", request_contact: true\}
        "Меню" -> /Main

state: GetPhoneNumber
    event: telegramSendContact
    script:
        $client.phonenumber = $request.rawRequest.message.contact.phonenumber;
    a: Спасибо! Наш менеджер свяжется с вами по номеру телефона \{\{ $client.phone_number \}\}.
```