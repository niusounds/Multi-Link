const PORT = 50201
const TYPE_PING = 0
const broadcastAddress = '192.168.10.255'

const EventEmitter = require('events');
const dgram = require('dgram');

module.exports = class UDPInterface extends EventEmitter {

  /**
   * @param {String} broadcastAddress ブロードキャスト送信に使用するIPアドレス 
   * @param {Number} port メッセージを送る先のポート番号
   */
  constructor(broadcastAddress, port) {

    if (!broadcastAddress || !port)
      throw new Error('broadcastAddress and port must be specified')

    super()
    this.broadcastAddress = broadcastAddress
    this.port = port
  }

  /**
   * メッセージの受信を開始する。
   */
  start() {

    if (this.socket) {
      return
    }

    this.socket = dgram.createSocket('udp4')
    this.socket.on('message', (msg, rinfo) => {

      // msg: Uint8Array => Buffer => String に変換
      let json = Buffer.from(msg.buffer).toString('utf8')
      let message = JSON.parse(json)

      console.log('receive', message)

      // typeにより分岐
      switch (message.type) {

        // PINGのレスポンス
        case TYPE_PING:
          return this.emit('ping', message.data)
      }
    })
    this.socket.on('error', err => {
      console.error(err)
      this.stop()
    })
    this.socket.bind()
  }

  /**
   * メッセージの受信を停止する。
   */
  stop() {

    if (this.socket) {
      this.socket.close()
      this.socket = null
    }

  }

  /**
   * メッセージを送信する。
   * @param {String|Buffer} msg 
   */
  async send(msg) {
    return new Promise((resolve, reject) => {
      this.socket.send(msg, this.port, this.broadcastAddress, (err, bytes) => {
        if (err) {
          reject(err)
        } else {
          resolve(bytes)
        }
      });
    })
  }

  /**
   * オブジェクトを送信する。
   * @param {object} data 
   */
  async sendObject(data) {

    // JSON文字列に変換
    let msg = JSON.stringify(data)
    return this.send(msg, this.port, this.broadcastAddress)

  }

}
