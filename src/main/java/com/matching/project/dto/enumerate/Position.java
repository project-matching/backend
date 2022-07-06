package com.matching.project.dto.enumerate;

public enum Position {
    BACKEND, FRONTEND, FULLSTACK, PRODUCT_MANAGER, DESIGNER;
    
    
    // 해당 String이 Enum에 있는지 판단하는 메소드
    public static boolean contains(String value) {
        for (Position p : Position.values()) {
            if (p.name().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
