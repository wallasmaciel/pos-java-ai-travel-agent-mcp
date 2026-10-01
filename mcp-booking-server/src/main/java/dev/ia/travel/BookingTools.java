package dev.ia.travel;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class BookingTools {
    @Inject
    BookingService bookingService;

    @Tool(name = "getBookingDetails", description = "Obtém os detalhes completos de uma reserva pelo ID.")
    public String getBookingDetails(
            @ToolArg(description = "O ID numérico único da reserva (ex: 12345)")
            long bookingId
    ) {
        return bookingService.getBookingDetails(bookingId)
            .map(Booking::toString)
            .orElse("Reserva com ID " + bookingId + " não encontrada.");
    }

    @Tool(name = "cancelBooking", description = "Cancela uma reserva pelo ID quando o usuário autenticado é o titular.")
    public String cancelBooking(
            @ToolArg(description = "ID da reserva a cancelar")
            long bookingId,
            @ToolArg(description = "Usuário que está tentando cancelar a reserva")
            String name
    ) {
        return bookingService.cancelBooking(bookingId, name)
            .map(booking -> "Reserva " + bookingId + " cancelada com sucesso. Status atual: " + booking.status())
            .orElse("Não foi possível cancelar a reserva. Verifique se o ID da reserva e o sobrenome do cliente estão corretos.");
    }

    @Tool(name = "listPackagesByCategory", description = "Lista pacotes de viagem por categoria.")
    public String listPackagesByCategory(
            @ToolArg(description = "Categoria utilizada como filtro para pacotes")
            Category category
    ) {
        List<Booking> packages = bookingService.findPackagesByCategory(category);
        if (packages.isEmpty()) {
            return "Nenhum pacote encontrado para a categoria: " + category;
        }

        return "Pacotes encontrados para a categoria '" + category + "'" +
            packages.stream().map(Booking::destination).toList().toString();
    }
}
