const AUTHENTICATION = "AUTHENTICATION";
const DATA = "DATA";
const ERROR = "ERROR";

const HIT_COUNT = "HIT_COUNT";
const MISS_COUNT = "MISS_COUNT";
const LOAD_COUNT = "LOAD_COUNT";
const EVICTION_COUNT = "EVICTION_COUNT";
const CACHE_SIZE = "CACHE_SIZE";
const AUTHENTICATED = "AUTHENTICATED";

var ws;

function init() {
    ws = new WebSocket("ws://" + document.location.host + "/cache");
    ws.onopen = function () {
        console.log("WebSocket open...");
    };
    ws.onmessage = function (e) {
        handle(JSON.parse(e.data));
    };
    ws.onclose = function () {
        close("Connection closed!");
    };
}

function close(msg) {
    console.log("WebSocket close...");
    ws = null;
    showLoginPanel();
    alert(msg);
}

function handle(msg) {
    switch (msg.type) {
        case AUTHENTICATION:
            if (msg.data[AUTHENTICATED]) {
                applyData(msg.data);
                showCachePanel();
            } else {
                alert("Invalid username or password!");
            }
            break;
        case DATA:
            applyData(msg.data);
            break;
        case ERROR:
            close("Internal server error!");
    }
}

function login() {
    var username = document.getElementById('inputUsername').value;
    var password = document.getElementById('inputPassword').value;

    if (isBlank(username) || isBlank(password)) {
        alert("Enter your username and password!");
        return;
    }

    if (ws === null) {
        init();
    }

    ws.send(JSON.stringify({"type": AUTHENTICATION, "data": {"username": username, "password": password}}));
}

function showCachePanel() {
    document.getElementById('loginPanel').style.display = "none";
    document.getElementById('cachePanel').style.display = "block";
}

function showLoginPanel() {
    document.getElementById('loginPanel').style.display = "block";
    document.getElementById('cachePanel').style.display = "none";
}

function applyData(arr) {
    document.getElementById('cacheSize').textContent = arr[CACHE_SIZE];
    document.getElementById('hitCount').textContent = arr[HIT_COUNT];
    document.getElementById('missCount').textContent = arr[MISS_COUNT];
    document.getElementById('loadCount').textContent = arr[LOAD_COUNT];
    document.getElementById('evictionCount').textContent = arr[EVICTION_COUNT];
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function alert(msg) {
    document.getElementById('btn').disabled = true;
    document.getElementById('failureMessage').textContent = msg;
    document.getElementById('alert').style.opacity = 1;
    setTimeout(function(){
        document.getElementById('alert').style.opacity = 0;
        document.getElementById('btn').disabled = false;
    }, 1000);
}