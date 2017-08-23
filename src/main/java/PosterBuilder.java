public class PosterBuilder {
    private InfoBot bot;

    public PosterBuilder setBot(InfoBot bot) {
        this.bot = bot;
        return this;
    }

    public Poster createPoster() {
        Poster poster = new Poster();
        poster.setBot(this.bot);
        return poster;
    }
}