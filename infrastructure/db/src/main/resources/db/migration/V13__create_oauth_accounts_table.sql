CREATE TABLE oauth_accounts (
    oauth_account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,              -- OAuthProvider.name()
    provider_user_id VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 로그인할 때 provider 계정으로 회원을 찾는다
CREATE UNIQUE INDEX uk_oauth_accounts_provider_provider_user_id
    ON oauth_accounts (provider, provider_user_id);

-- 한 회원에게 같은 provider 계정이 둘 연결되지 않게 막는다
CREATE UNIQUE INDEX uk_oauth_accounts_member_id_provider
    ON oauth_accounts (member_id, provider);
