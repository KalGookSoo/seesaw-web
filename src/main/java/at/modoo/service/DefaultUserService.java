package at.modoo.service;

import at.modoo.command.CreateUserCommand;
import at.modoo.command.UpdateUserCommand;
import at.modoo.entity.Authority;
import at.modoo.entity.ContactNumber;
import at.modoo.entity.Email;
import at.modoo.entity.User;
import at.modoo.repository.UserRepository;
import at.modoo.repository.UserSearchRepository;
import at.modoo.search.UserSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * @see UserService
 */
@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * @see UserService#createUser(CreateUserCommand)
     */
    @Override
    public User createUser(@NonNull CreateUserCommand command) {
        Email email = new Email(command.emailId(), command.emailDomain());
        ContactNumber contactNumber = new ContactNumber(command.firstContactNumber(), command.middleContactNumber(), command.lastContactNumber());
        User user = User.create(command.username(), command.password(), command.name(), email, contactNumber);
        user.changePassword(passwordEncoder.encode(user.getPassword()));
        Authority authority = Authority.create("ROLE_USER", user);
        user.addAuthority(authority);
        return userRepository.save(user);
    }

    /**
     * @see UserService#createAdmin(CreateUserCommand)
     */
    @Override
    public User createAdmin(@NonNull CreateUserCommand command) {
        Email email = new Email(command.emailId(), command.emailDomain());
        ContactNumber contactNumber = new ContactNumber(command.firstContactNumber(), command.middleContactNumber(), command.lastContactNumber());
        User user = User.create(command.username(), command.password(), command.name(), email, contactNumber);
        user.changePassword(passwordEncoder.encode(user.getPassword()));
        Authority authority = Authority.create("ROLE_ADMIN", user);
        user.addAuthority(authority);
        return userRepository.save(user);
    }

    /**
     * @see UserService#update(String, UpdateUserCommand)
     */
    @Override
    public User update(@NonNull String id, @NonNull UpdateUserCommand command) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        String name = command.name();
        Email email = new Email(command.emailId(), command.emailDomain());
        ContactNumber contactNumber = new ContactNumber(command.firstContactNumber(), command.middleContactNumber(), command.lastContactNumber());
        user.update(name, email, contactNumber);
        return userRepository.save(user);
    }

    /**
     * @see UserService#findById(String)
     */
    @Transactional(readOnly = true)
    @Override
    public User findById(@NonNull String id) {
        return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * @see UserService#findByUsername(String)
     */
    @Override
    public User findByUsername(@NonNull String username) {
        return userRepository.findByUsername(username).orElseThrow(NoSuchElementException::new);
    }

    /**
     * @see UserService#findAll(Pageable)
     */
    @Transactional(readOnly = true)
    @Override
    public Page<User> findAll(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * @see UserService#findAll(UserSearch)
     */
    @Transactional(readOnly = true)
    @Override
    public Page<User> findAll(@NonNull UserSearch search) {
        return userSearchRepository.search(search);
    }

    /**
     * @see UserService#delete(String)
     * 삭제 연산은 권한을 가진자에게만 허용할 예정이라 노출해도 무방할 것 같다고 판단하여 예외 처리를 하였습니다.
     */
    @Override
    public void delete(@NonNull String id) {
        User user = userRepository.getReferenceById(id);
        user.removeAuthorities();
        userRepository.deleteById(id);
    }

    /**
     * @see UserService#updatePassword(String, String, String)
     */
    @Override
    public void updatePassword(@NonNull String id, @NonNull String originPassword, @NonNull String newPassword) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        if (!passwordEncoder.matches(originPassword, user.getPassword())) {
            throw new IllegalArgumentException("계정 정보가 일치하지 않습니다.");
        }
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
