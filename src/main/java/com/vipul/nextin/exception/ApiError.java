package com.vipul.nextin.exception;

import java.util.Map;

public record ApiError(int status, String error, Map<String, String> errors, String path) {
}
