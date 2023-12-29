package domain.enum_class;

public enum BoardCategory {
	FREE, GREETING, GOLD;

	public static BoardCategory of(String category) {
		// of 메소드를 통해 String 타입의 category를 BoardCategory 타입으로 변환하는 기능 추가
		if (category.equalsIgnoreCase("free"))
			return BoardCategory.FREE;
		else if (category.equalsIgnoreCase("greeting"))
			return BoardCategory.GREETING;
		else if (category.equalsIgnoreCase("gold"))
			return BoardCategory.GOLD;
		else
			return null;
	}
}
