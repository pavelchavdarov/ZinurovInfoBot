public class MessengerBuilder {
    private InfoBot bot;

    public MessengerBuilder setBot(InfoBot bot) {
        this.bot = bot;
        return this;
    }

    public Messenger createMessenger() {
        Messenger messenger =  new Messenger();
        messenger.setBot(this.bot);
        return messenger;

    }
}