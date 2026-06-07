package com.footballplatform.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.support.RequestContextUtils;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   Model model) {
        System.out.println("Runtime error: " + ex.getMessage());

        if ("status".equals(ex.getName()) && ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            RequestContextUtils.getOutputFlashMap(request).put("errorMessage", "Filter không hợp lệ.");
            RequestContextUtils.saveOutputFlashMap("/home", request, response);
            return "redirect:/home";
        }

        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", "Trang không tồn tại");
        model.addAttribute("errorMessage", "Liên kết bạn truy cập không hợp lệ hoặc đã thay đổi.");
        return "error/404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex,
                                        HttpServletResponse response,
                                        Model model) {
        System.out.println("Runtime error: " + ex.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorTitle", "Đã có lỗi xảy ra");
        model.addAttribute("errorMessage", "Yêu cầu không hợp lệ hoặc không thể xử lý vào lúc này.");
        return "error/500";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                       HttpServletResponse response,
                                                       Model model) {
        System.out.println("Runtime error: " + ex.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorTitle", "Yêu cầu không hợp lệ");
        model.addAttribute("errorMessage", "Yêu cầu bạn gửi lên thiếu tham số hoặc không đúng định dạng.");
        return "error/400";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex,
                                        HttpServletResponse response,
                                        Model model) {
        System.out.println("Runtime error: " + ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", "Không tìm thấy trang");
        model.addAttribute("errorMessage", "Trang bạn đang tìm không tồn tại hoặc đã bị di chuyển.");
        return "error/404";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex,
                                         HttpServletResponse response,
                                         Model model) {
        System.out.println("Runtime error: " + ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", "Không tìm thấy dữ liệu");
        model.addAttribute("errorMessage", "Nội dung bạn cần hiện không có sẵn.");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex,
                                  HttpServletResponse response,
                                  Model model) {
        System.out.println("Runtime error: " + ex.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorTitle", "Đã có lỗi xảy ra");
        model.addAttribute("errorMessage", "Hệ thống đang gặp sự cố tạm thời. Vui lòng thử lại sau.");
        return "error/500";
    }
}
