const { io } = require("socket.io-client");
const socket = io("http://localhost:3000");
const readline = require("readline");

let rl = null;
let username = null;
let roomId = null;

socket.on("connect", () => {
    console.log("connected");
    choice();
});

socket.on("chat message", (payload) => {
    console.log(`\n${payload.name}: ${payload.msg}`);
    if (!username) getName(); else sendMsg();
});

socket.on('create room', (id) => {
    roomId = id;
    console.log(`Room ${id} created`);
    getName();
});

socket.on('join room', (payload) => {
    if (payload.msg === 'success') {
        console.log(`Room ${payload.roomId} joined`);
        roomId = payload.roomId;
        getName();
    } else {
        console.log("Room not found");
        choice();
    }
});

const choice = () => {
    console.log("1. Create room");
    console.log("2. Join room");
    console.log("3. Exit");

    if (rl) rl.close();
    rl = readline.createInterface({
        input: process.stdin, output: process.stdout,
    });
    rl.question("Choose: ", (val) => {
        switch (val) {
            case "1":
                socket.emit("create room");
                break;
            case "2":
                joinRoom();
                break;
            case "3":
                process.exit();
                break;
            default:
                choice();
        }
    });
}

const joinRoom = () => {
    if (rl) rl.close();

    rl = readline.createInterface({
        input: process.stdin, output: process.stdout,
    });
    rl.question("Enter room id: ", (id) => {
        const payload = { name: username, roomId: id };
        socket.emit("join room", payload);
    });
}

const getName = () => {
    if (rl) rl.close();

    rl = readline.createInterface({
        input: process.stdin, output: process.stdout,
    });
    rl.question("Enter your name: ", (name) => {
        const payload = { name, roomId };
        socket.emit("new user", payload);
        username = name;
        sendMsg();
    });
}

const sendMsg = () => {
    if (rl) rl.close();

    rl = readline.createInterface({
        input: process.stdin, output: process.stdout,
    });
    rl.question(">> ", (msg) => {
        const payload = { msg, name: username, roomId };
        socket.emit("chat message", payload);
        sendMsg();
    });
};