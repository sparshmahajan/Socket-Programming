const { Server } = require("socket.io");
const io = new Server(3000);

const clients = {};
io.on("connection", (socket) => {
    clients[socket.id] = { name: null, socket };

    socket.on('new user', (name) => {
        console.log("New user connected: " + name);
        clients[socket.id].name = name;
        broadcastMsg(socket, "has joined the chat");
    });

    socket.on("chat message", (msg) => {
        broadcastMsg(socket, msg);
    });
});

const broadcastMsg = (socket, msg) => {
    const sender = clients[socket.id].name;
    const otherClients = Object.keys(clients).filter(id => id !== socket.id);

    otherClients.forEach((client) => {
        const payload = {
            msg, name: sender,
        }
        clients[client].socket.emit("chat message", payload);
    });
}
