const { io } = require('socket.io-client')
const socket = io('http://localhost:3000')
const readline = require('readline')

let rl = null
socket.on('connect', () => {
    console.log('connected')
    sendMsg()
})

socket.on('chat message', (msg) => {
    console.log('\nServer: ' + msg)
    sendMsg()
})

const sendMsg = () => {
    if (rl) rl.close()

    rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
    })
    rl.question('>> ', (msg) => {
        socket.emit('chat message', msg)
        sendMsg()
    })
}
