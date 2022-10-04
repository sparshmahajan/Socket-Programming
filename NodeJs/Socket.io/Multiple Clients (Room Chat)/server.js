const { Server } = require("socket.io");
const io = new Server(3000);

console.log("listening on port 3000");

const rooms = {};
io.on("connection", (socket) => {

    socket.on("disconnect", () => {
        const roomKeys = Object.keys(rooms);
        roomKeys.forEach((room) => {
            const clients = rooms[room].clients;

            if(!clients[socket.id]) return;

            console.log(clients[socket.id].name, "disconnected");
            delete clients[socket.id];

            if (Object.keys(clients).length === 0) {
                console.log("room", room, "deleted");
                delete rooms[room];
            }
        })

    })

    socket.on('new user', (payload) => {
        console.log("New user connected: " + payload.name);
        const clients = rooms[payload.roomId].clients;
        clients[socket.id].name = payload.name;
        broadcastMsg(socket, clients, "has joined the chat");
    });

    socket.on("chat message", (payload) => {
        const clients = rooms[payload.roomId].clients;
        broadcastMsg(socket, clients, payload.msg);
    });

    socket.on("create room", () => {
        const id = generateId();
        const clients = {};
        clients[socket.id] = { socket, name: null };
        rooms[id] = { clients };
        socket.emit("create room", id);
    });

    socket.on("join room", (payload) => {
        const room = rooms[payload.roomId];
        if (room) {
            const clients = room.clients;
            clients[socket.id] = { socket, name: null };
            rooms[payload.roomId] = { clients };
            socket.emit("join room", { msg: "success", roomId: payload.roomId });
        } else {
            socket.emit("join room", { msg: "fail", roomId: payload.roomId });
        }
    });
});

const broadcastMsg = (socket, clients, msg) => {
    const sender = clients[socket.id].name;
    const otherClients = Object.keys(clients).filter(id => id !== socket.id);

    otherClients.forEach((client) => {
        const payload = {
            msg, name: sender,
        }
        clients[client].socket.emit("chat message", payload);
    });
}

const generateId = () => {
    const roomId = String(Math.round(Math.random() * 10000)).padStart(4, "0");
    const match = Object.keys(rooms).find((id) => id === roomId);

    return match ? generateId() : roomId;
}