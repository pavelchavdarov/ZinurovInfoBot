public class MessagerBuilder {
    private InfoBot bot;

    public MessagerBuilder setBot(InfoBot bot) {
        this.bot = bot;
        return this;
    }

    public Messager createMessager() {
        Messager messager = new Messager();
        messager.setBot(this.bot);
        return messager;
    }
}