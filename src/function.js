function deleteFromCart(name) {
    var $session = $jsapi.context().session;
    for (var i = 0; i < $session.cart.length; i++) {
        var current_position = $session.cart[i];
        if (current_position.name === name) {
            $session.cart.splice(i, 1);
        }
    }
    log('!!!!!! CART: ' + JSON.stringify($session.cart));
}

function getTotalSum() {
    var totalSum = 0;
    var $session = $jsapi.context().session;

    for (var i = 0; i < $session.cart.length; i++) {
        var current_position = $session.cart[i];
        for (var id = 1; id < Object.keys(plants).length + 1; id++) {
            if (current_position.name === plants[id].value.name) {
                totalSum += current_position.price * current_position.quantity;
            }
        }
    }
    log("!!!!!!!!!!!! totalSum = " + totalSum);
    return totalSum;
}

function editText(messageId, text) {
