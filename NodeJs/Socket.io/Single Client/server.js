const { Server } = require('socket.io')
const io = new Server(3000)
const readline = require("readline");

let rl = null

io.on('connection', (socket) => {
    console.log('a user connected')
    sendMsg()

    socket.on('chat message', (msg) => {
        console.log('\nClient: ' + msg)
        sendMsg()
    })
})

const sendMsg = () => {
    if (rl) rl.close()

    rl = readline.createInterface({
        input: process.stdin, output: process.stdout,
    })
    rl.question('>> ', (msg) => {
        io.emit('chat message', msg)
        sendMsg()
    })
}