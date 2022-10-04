const { io } = require("socket.io-client");
const socket = io("http://localhost:3000");
const readline = require("readline");

let rl = null;
let username = null;
socket.on("connect", () => {
    console.log("connected");
    getName();
});

socket.on("chat message", (payload) => {
    console.log(`\n${payload.name}: ${payload.msg}`);
    if (username == null) getName(); else sendMsg();
});

const sendMsg = () => {
    if (rl) rl.close();

    rl = readline.createInterface({
        input: process.stdin, output: process.stdout,
    });
    rl.question(">> ", (msg) => {
        socket.emit("chat message", msg);
        sendMsg();
    });
};

const getName = () => {
    if (rl) rl.close();

    rl = readline.createInterface({
        input: process.stdin, output: process.stdout,
    });
    rl.question("Enter your name: ", (name) => {
        socket.emit("new user", name);
        username = name;
        sendMsg();
    });
}
