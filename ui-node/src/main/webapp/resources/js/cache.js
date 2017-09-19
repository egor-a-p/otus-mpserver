var cacheWS;

const LOGIN_MSG_TYPE = "LOGIN_MSG";
const CACHE_MSG_TYPE = "CACHE_MSG";
const CONNECTED_KEY = "CONNECTED_KEY";
const HIT_COUNT_KEY = "HIT_COUNT_KEY";
const MISS_COUNT_KEY = "MISS_COUNT_KEY";
const LOAD_COUNT_KEY = "LOAD_COUNT_KEY";
const EVICTION_COUNT_KEY = "EVICTION_COUNT_KEY";
const SIZE_KEY = "SIZE_KEY";;

function login() {
    hideAlert();
    var username = document.getElementById('inputUsername').value;
    var password = document.getElementById('inputPassword').value;

    if (isBlank(username) || isBlank(password)) {
        showAlert("Enter your username and password!");
        return;
    }

    cacheWS = new WebSocket("ws://" + document.location.host + "/cache");

    cacheWS.onopen = function () {
        cacheWS.send(JSON.stringify({
                "type": LOGIN_MSG_TYPE, "data": {"username": username, "password": password}
            }
        ));
    };

    cacheWS.onmessage = function (event) {
        var msg = JSON.parse(event.data);
        switch (msg.type) {
            case LOGIN_MSG_TYPE:
                if (!msg.data[CONNECTED_KEY]) {
                    showAlert("Illegal username or password!");
                    cacheWS.close();
                    break;
                } else {
                    document.getElementById('loginPanel').style.display = "none";
                    document.getElementById('cachePanel').style.display = "block";
                }
            case CACHE_MSG_TYPE:
                updateStats(msg.data);
                break;
            default:
                close();
        }
    };

    cacheWS.onclose = function () {
        close();
    }
}

function close() {
    document.getElementById('loginPanel').style.display = "block";
    document.getElementById('cachePanel').style.display = "none";
    cacheWS.close();
}

function updateStats(arr) {
    document.getElementById('cacheSize').textContent = arr[SIZE_KEY];
    document.getElementById('hitCount').textContent = arr[HIT_COUNT_KEY];
    document.getElementById('missCount').textContent = arr[MISS_COUNT_KEY];
    document.getElementById('loadCount').textContent = arr[LOAD_COUNT_KEY];
    document.getElementById('evictionCount').textContent = arr[EVICTION_COUNT_KEY];
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function showAlert(msg) {
    document.getElementById('failureMessage').textContent = msg;
    document.getElementById('alert').style.display = "block";
}

function hideAlert() {
    document.getElementById('failureMessage').textContent = "";
    document.getElementById('alert').style.display = "none";
}