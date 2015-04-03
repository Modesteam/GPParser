package libraries;

public @interface BelongsTo {

	Class<?> entity();

	String reference();

}
