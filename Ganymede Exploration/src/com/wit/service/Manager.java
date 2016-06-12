package com.wit.service;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Manages network requests.
 */
public abstract class Manager {
	private static final class HeaderInterceptor implements Interceptor {
		@Override
		public okhttp3.Response intercept(final Chain chain) throws IOException {
			final Request originalRequest = chain.request();
			final Request.Builder newRequestBuilder = originalRequest.newBuilder();

			newRequestBuilder.addHeader("x-commander-email", "witkurowski@gmail.com");

			final Request newRequest = newRequestBuilder.build();
			final okhttp3.Response response = chain.proceed(newRequest);

			return response;
		}
	}

	/**
	 * The base URL of all network calls.
	 */
	private static final String BASE_URL = "http://challenge2.airtime.com:10001";

	/**
	 * The properly configured instance of {@link Retrofit} to use to generate network call
	 * implementations.
	 */
	private final Retrofit retrofit;

	/**
	 * Creates a new {@link Manager}.
	 */
	public Manager() {
		final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

		retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
		retrofitBuilder.baseUrl(Manager.BASE_URL);

		final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
		final HeaderInterceptor headerInterceptor = new HeaderInterceptor();

		okHttpClientBuilder.addInterceptor(headerInterceptor);

		final OkHttpClient okHttpClient = okHttpClientBuilder.build();

		retrofitBuilder.client(okHttpClient);

		this.retrofit = retrofitBuilder.build();
	}

	/**
	 * Creates an instance of an implementation class derived from an interface.
	 *
	 * @param classToCreateInstanceOf
	 *            The {@link Class} of the interface to derive an implementation instance of.
	 * @return An instance of a derived implementation class.
	 */
	protected <T> T create(final Class<T> classToCreateInstanceOf) {
		final T createdClassInstance = this.retrofit.create(classToCreateInstanceOf);

		return createdClassInstance;
	}
}