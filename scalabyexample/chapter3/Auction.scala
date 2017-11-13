import scala.actors.{Actor, TIMEOUT}
import java.util.Date

/**
  * coordinate the bidding on one item for auction actors
  * @param seller a seller actor which needs to be notified when the auction is over
  * @param minBid a minimal bid
  * @param closing  the date when the auction is to be closed
  */
class Auction(seller: Actor, minBid: Int, closing: Date) extends Actor {
  val timeToShutdown = 36000000   // msec
  val bidIncrement = 10

  /**
    * the behavior of the actor, it repeatedly selects (using <code>receiveWithin</code>) a message and reacts to it
    * until the auction is closed
    */
  def act(): Unit = {
    var maxBid = minBid - bidIncrement
    var maxBidder: Actor = null
    var running = true

    while (running) {
      receiveWithin((closing.getTime() - new Date().getTime())) {
        case Offer(bid, client) =>
          if (bid >= maxBid + bidIncrement) {
            if (maxBid >= minBid) maxBidder ! BeatenOffer(bid)
            maxBid = bid; maxBidder = client; client ! BestOffer
          } else {
            client ! BeatenOffer(maxBid)
          }
        case Inquire(client) =>
          client ! Status(maxBid, closing)
        case TIMEOUT =>
          if (maxBid >= minBid) {
            val reply = AuctionConcluded(seller, maxBidder)
            maxBidder ! reply; seller ! reply
          } else {
            seller ! AuctionFailed
          }
          receiveWithin(timeToShutdown) {
            case Offer(_, client) => client ! AuctionOver
            case TIMEOUT => running = false
          }
      }
    }
  }
}
